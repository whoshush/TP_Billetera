# Registro de cambios — Conversación completa

Documentación de **todo lo modificado** desde el inicio de la refactorización del TP Billetera Virtual (`billete.ar`).

---

## Archivos que NO se tocaron (por consigna)

| Archivo | Motivo |
|---------|--------|
| `Principal.java` | Cliente provisto — prohibido modificar |
| `BilleteraTest.java` | JUnit provisto — prohibido modificar |
| `Utilitarios.java` | Clase provista — prohibido modificar |

---

## Archivo nuevo creado

### `RegistroOperaciones.java`

- **Responsabilidad:** registro global de operaciones e IDs.
- **Contenido:**
  - `HashMap<String, Operacion>` global
  - Contadores separados: `TR-N` (transferencias) y `N` (inversiones)
  - `generarIdTransferencia()`, `generarIdInversion()`
  - `registrar(Operacion)` — valida que no sea null
  - `consultarHistorialGlobal()` — usa **`Iterator<Operacion>`**
  - `obtenerCantidadOperaciones()`

---

## Refactor principal (plan de 8 tareas)

### `Operacion.java`

| Antes | Después |
|-------|---------|
| Constructor sin `aprobada`; default `false` | Constructor `(id, monto, fecha, boolean aprobada)` |
| `setAprobada()` | **Eliminado** — estado fijado en constructor |
| Sin validación IREP | Valida `id`, `fecha`, `monto > 0` en constructor |
| — | `esInversion()` → default `false` |
| — | `intentarPrecancelar()` → default `false` |
| — | `intentarProcesarVencimientoHoy()` → no-op en base |

### `Transferencia.java`

- Constructor recibe `boolean aprobada`.
- `toString()` propio (delega a `obtenerDetalle()`).
- Formato historial sin cambios funcionales.

### `Inversion.java`

- Constructor recibe `boolean aprobada`.
- `esInversion()` → `true`.
- Campo `vencida` para bonus track.
- `venceHoy()`, `precancelarEn()`, `procesarVencimiento()`.
- `intentarPrecancelar()` / `intentarProcesarVencimientoHoy()` (polimorfismo).
- **`aplicarFactorRentabilidadCuenta(double)`** — multiplica por `origen.factorRentabilidadInversion()` *(cierre auditoría)*.

### `RentaFija.java`, `Divisa.java`, `FondoEmpresarial.java`

- Constructores con parámetro `aprobada`.
- `toString()` con campos específicos *(ampliado en cierre auditoría)*.
- `calcularResultado()` usa `aplicarFactorRentabilidadCuenta()` *(cierre auditoría)*.

### `Cuenta.java` (abstracta)

- **IREP en constructor:** cvu, alias, dniTitular no vacíos.
- **Nuevos abstractos:** `validarDeposito()`, `obtenerTipoCuenta()`.
- **`admiteFondoLiquidez()`** → default `false`.
- **`factorRentabilidadInversion()`** → default `1.0` *(cierre auditoría)*.
- **`validarOperacionInversion(monto)`** — delega a `validarOperacion`; inversiones lo usan en lugar de `validarOperacion` *(cierre auditoría)*.
- **`realizarTransferencia(...)`** — flujo **validar → crear → ejecutar**:
  - Valida origen y destino **antes** de debitar.
  - Transferencia rechazada con `aprobada = false` registrada en origen, destino y global.
- Métodos de inversión delegados: `realizarInversionRentaFija/Divisa/Liquidez`.
- `precancelarInversion()` vía `esInversion()` + `intentarPrecancelar()` (sin `instanceof`).
- `consultarHistorial()`, `obtenerCantidadOperaciones()`, `obtenerDescripcion()`, `obtenerDescripcionConVolumen()`.

### `CuentaRegular.java`

- `validarDeposito()` — límite $5.000.000 (movido desde `depositar()`).
- `depositar()` en base ya no valida límite.
- `obtenerTipoCuenta()` → `"Regular"`.
- `toString()` propio.

### `CuentaPremium.java`

- `validarDepositoInicial()` estático (mínimo $500.000 al crear).
- `obtenerTipoCuenta()` → `"Premium"`.
- **`validarOperacion()`** — exige saldo ≥ $500.000 tras extracción/transferencia *(cierre auditoría)*.
- **`validarOperacionInversion()`** — solo saldo suficiente (inversiones no violan mínimo de disponible; capital sigue “en cuenta”).
- **`factorRentabilidadInversion()`** → `1.0` (compatible con JUnit provisto).
- `toString()` propio.

### `CuentaCorporativa.java`

- IREP: empresa no nula.
- `admiteFondoLiquidez()` → `true`.
- **`factorRentabilidadInversion()`** → `1.05` (bonus 5% corporativo) *(cierre auditoría)*.
- `toString()` incluye razón social de empresa.

### `Usuario.java`

- **IREP en constructor:** dni, nombre, teléfono, email obligatorios.
- **`totalInvertido`** — acumulador **O(1)** (`sumarInvertido`, `restarInvertido`, `obtenerTotalInvertido`).
- Búsqueda: `buscarCuentaPorAlias`, estáticos `buscarCuentaEnSistema`, `buscarCvuPorAlias`, `aliasDisponible`.
- `obtenerCuentasFormateadas()` — usa **`forEach`**.
- `consultarHistorial()` — agrega historial de todas sus cuentas.
- Factory de cuentas: `crearCuentaRegular/Premium/Corporativa`.

### `Empresa.java`

- **IREP en constructor:** cuit y razón social obligatorios.
- Campos guardados: `telefono`, `email`, `nombreContacto`.
- Lista `autorizados` con `agregarAutorizado`, `estaAutorizado`, `obtenerAutorizados`.

### `Billetera.java` — de ~470 a ~297 líneas (fachada)

| Eliminado | Reemplazo |
|-----------|-----------|
| 6 mapas (`operacionesGlobales`, `cuentasPorCvu`, `aliasACvu`, `autorizadosPorEmpresa`, …) | Solo `usuarios`, `empresas` + `RegistroOperaciones` |
| Validaciones IREP en fachada | Delegadas a constructores de TADs |
| Lógica de transferencias/inversiones | Delegada a `Cuenta` / `Usuario` / `Empresa` |
| `instanceof` para tipos | Polimorfismo: `obtenerTipoCuenta()`, `admiteFondoLiquidez()`, `esInversion()` |
| Recorrido O(n) para total invertido | `usuario.obtenerTotalInvertido()` O(1) |

- **`procesarInversionesQueVencenHoy()`** implementado (bonus).
- **`cuentasConMayorVolumen`** usa **`forEach`** + `obtenerDescripcionConVolumen()`.

### `IBilletera.java`

- **Única modificación permitida:** descomentado `void procesarInversionesQueVencenHoy();`

---

## Cierre de auditoría (segunda ronda de cambios)

### `FondoEmpresarial.java`

- Deja tasa hardcodeada `0.08/365`.
- Usa **`Utilitarios.consultarCotizacion("FLE")`**.
- Fórmula: `monto * (0.08 + cotizacionFLE) / 365 * dias`.
- Guarda `cotizacionFLE` al crear (para `toString`).
- `toString()`: activo FLE, tasa base, cotización.

### `RentaFija.java` — `toString()`

- Incluye `tasaInteres`.

### `Divisa.java` — `toString()`

- Incluye `monedaReferencia`, `tasa`, `cotizacionInicial`.

### `RegistroOperaciones.java`

- Validación null en `registrar()`.
- Flujo `aprobada`: transferencias rechazadas `false`, aprobadas `true`; inversiones solo exitosas con `true`.

---

## Documentación actualizada

### `docs/IREP_n_TAD.txt`

- Billetera simplificada (solo `usuarios`, `empresas`, `registroOperaciones`).
- Nuevo IREP de `RegistroOperaciones`.
- `Usuario`: `totalInvertido >= 0`.
- `Empresa`: `autorizados != null`.

### `docs/OrdenesAlgebra.txt`

- Análisis transferencia con búsqueda lineal `O(U×C)`.
- Incluye `validarOperacion`, `validarDeposito`, `extraer`, `depositar` por subtipo.
- `obtenerTotalInvertido` documentado como **O(1)**.

---

## Bugs corregidos durante el refactor

| Bug | Solución |
|-----|----------|
| Transferencia a Regular > $5M debitaba origen antes de validar destino | `validarDeposito()` en destino **antes** de `extraer()` |
| `setAprobada()` rompía encapsulamiento | `aprobada` en constructor de `Operacion` |
| `obtenerTotalInvertido` era O(cuentas × operaciones) | Acumulador `totalInvertido` en `Usuario` |
| 6 HashMaps redundantes en `Billetera` | Delegación + búsqueda lineal de CVU/alias |
| Uso de `instanceof` en 6 lugares | Métodos polimórficos |

---

## Requisitos técnicos de consigna — dónde quedaron

| Requisito | Ubicación |
|-----------|-----------|
| `StringBuilder` | `Operacion`, `Transferencia`, `Inversion`, `Cuenta`, `Usuario`, `Billetera`, subclases |
| `Iterator` | `RegistroOperaciones.consultarHistorialGlobal()` |
| `forEach` | `Usuario.obtenerCuentasFormateadas()`, `Billetera.cuentasConMayorVolumen()` |
| Herencia + polimorfismo | Jerarquías `Cuenta`, `Operacion`/`Inversion` |
| Interfaces + override | `IBilletera`, `@Override` en subclases |
| Clases/métodos abstractos | `Cuenta`, `Operacion`, `Inversion` |
| `toString` en TADs | Todas las clases de dominio + `Billetera` |

---

## Estado final de verificación

| Prueba | Resultado |
|--------|-----------|
| `BilleteraTest` (15 tests) | **OK** |
| `Principal.main` | **OK** — simulación completa |

---

## Decisiones de diseño acordadas

1. **Premium `factorRentabilidadInversion()` = 1.0** — el bonus 1.05 está en **Corporativa** para no romper JUnit provisto.
2. **Mínimo $500k Premium** — aplica a transferencias/extracciones, no a inversiones (compatible con `Principal`).
3. **`IBilletera`, `Principal`, `BilleteraTest`, `Utilitarios`** — intactos salvo descomentar bonus en interfaz.

---

## Resumen por archivo

| Archivo | Acción |
|---------|--------|
| `RegistroOperaciones.java` | **Creado** |
| `Operacion.java` | Refactorizado |
| `Transferencia.java` | Refactorizado |
| `Inversion.java` | Refactorizado + factor rentabilidad |
| `RentaFija.java` | Refactorizado + toString |
| `Divisa.java` | Refactorizado + toString |
| `FondoEmpresarial.java` | Refactorizado + FLE + toString |
| `Cuenta.java` | Refactorizado + delegación transferencias/inversiones |
| `CuentaRegular.java` | Refactorizado |
| `CuentaPremium.java` | Refactorizado + reglas mínimo |
| `CuentaCorporativa.java` | Refactorizado + factor 1.05 |
| `Usuario.java` | Refactorizado + O(1) invertido |
| `Empresa.java` | Refactorizado + autorizados |
| `Billetera.java` | Refactorizado (fachada delgada) |
| `IBilletera.java` | Descomentado bonus |
| `docs/IREP_n_TAD.txt` | Actualizado |
| `docs/OrdenesAlgebra.txt` | Actualizado |
| `docs/REGISTRO_CAMBIOS.md` | Este archivo |
| `Principal.java` | Sin cambios |
| `BilleteraTest.java` | Sin cambios |
| `Utilitarios.java` | Sin cambios |
