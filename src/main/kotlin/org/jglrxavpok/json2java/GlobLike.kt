package org.jglrxavpok.json2java

object GlobLike {
    fun toRegex(glob: String): String {
        val result = StringBuilder()
        var index = 0
        loop@ while(index < glob.length) {
            val char = glob[index]
            when(char) {
                '*' -> {
                    if(index+1 < glob.length) {
                        val next = glob[index+1]
                        if(next == '*') {
                            result.append(".*")
                            index++
                            continue@loop
                        }
                    }
                    result.append("[^\\/]*")
                }
                '/' -> result.append("\\/")
                else -> result.append(char)
            }
            index++
        }
        return result.toString()
    }
}
