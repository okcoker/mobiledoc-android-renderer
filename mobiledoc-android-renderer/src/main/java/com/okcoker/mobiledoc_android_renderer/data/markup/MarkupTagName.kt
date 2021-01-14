package com.okcoker.mobiledoc_android_renderer.data.markup

enum class MarkupTagName(val type: String) {
    A("a"),
    B("b"),
    CODE("code"),
    EM("em"),
    I("i"),
    S("s"),
    STRONG("strong"),
    SUB("sub"),
    SUP("sup"),
    U("u"),
    // Custom added item so we can account for html text nodes
    // (ie text not surrounded by any of the above)
    SPAN("span")
}