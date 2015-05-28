# SysFig

What is this?  It's my attempt to merge together [system](https://github.com/danielsz/system) and [figwheel](https://github.com/bhauman/lein-figwheel).  Basically, I started off with the [system leiningen example](https://github.com/danielsz/system/tree/master/examples/leiningen) and added figwheel to it.  I may end up making it a lein template at some point.

# Running

To get the server running:

```
$ lein repl
sysfig.core=> (go)
```

To get figwheel running:

```
lein figwheel
```

Then open <http://localhost:3000> in your web browser.
