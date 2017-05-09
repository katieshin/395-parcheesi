# Parcheesi
### Jordan Timmerman and Katie Shin and Steven Vorbrich

...

## Getting started

To get `build` and `clean` aliases in your shell:

```sh
$ . mkaliases
```

To `build` a class and run its tests:

```sh
$ build ./path/to/class.java
# Example 1:
$ cd parcheesi/rule; build RulesChecker.java
# Example 2:
$ build parcheesi/rule/RulesChecker.java
# Scripts are directory robust.
```

To `clean` any lingering `.class` files (should not usually be needed):

```sh
$ clean
```

