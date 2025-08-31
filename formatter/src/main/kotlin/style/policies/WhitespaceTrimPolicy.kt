package style.policies

enum class WhitespaceTrimPolicy : Policy {
    NONE, // deja espacios al final de cada linea
    TRAILING, // borra los espacios de final de linea
}
