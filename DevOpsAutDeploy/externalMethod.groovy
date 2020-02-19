
def concat( String... p ) {
    String s = ""
    p.each { s += it + " " }
    return s
}

return this;