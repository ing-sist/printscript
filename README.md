## Configurar Git Hooks (una sola vez por clon)

Nuestros hooks corren **Spotless**, **Detekt** y **build** antes de cada commit.
Si algo falla, el commit se bloquea.

### macOS / Linux
```bash
./scripts/bootstrap.sh 
```
### Windows
```
sh scripts/bootstrap.sh
```
o
```
bash scripts/bootstrap.sh
```
