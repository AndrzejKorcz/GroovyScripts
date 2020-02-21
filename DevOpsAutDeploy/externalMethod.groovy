
def concatenate(String... p) {
    String s = ""
    p.each { s += it + " " }
    return s
}

def isNullOrEmpty(String s) {
    return !s?.trim()
}


return this;