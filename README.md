## Mobiledoc Android Renderer

Mobiledoc server and client rendering for [Mobiledoc-kit](https://github.com/bustlelabs/mobiledoc-kit).

This renderer will be used to render mobiledoc into native views. Currently testing with v0.3.1. There is currently no native renderer available at the moment. We can eventually use this for Audiomack World to do more custom things.

### Usage

From your main activity, for example:

```kotlin
val mobiledoc = getJsonDataFromYourAPIOrSomething()
val container = findViewById<LinearLayout>(R.id.container)
val renderer = MobileDocRenderer(mobiledoc)

renderer.render(this).result.forEach { v ->
    container.addView(v)
}

```

### Todo

- Figure out a clean way to allow developers to style views
- Do we need a custom MobileDocRenderer view?
- Figure out a clean way for developers to pass their own render functions for markups, atoms, cards and custom sections. (This kind of exists as arguments after `mobiledoc` but is untested)


### Sample App

This repo contains a sample app with static json that can be used to test the render output.

### Tests
`./gradlew test`