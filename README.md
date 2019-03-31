## Jakol
### Abstract
In my dyploma project I have a microservice to generate reports in different formats (excel, pdf and xml).
So I need a helper tool to generate sample xml files with much more readable and concise syntax, than standard java libs provide. 

### How to compile and run 
There are 2 Gradle tasks: one to compile your source file, and other to run it

To compile your source file run next task in project folder:
```
./gradlew :clean :runCompiler --args='path/to/source/file'
```
you can start with example source file and launch task with next argument:
```
./gradlew :clean :runCompiler --args='src/main/kotlin/source'
```

File is compiled normally if you see output like `BUILD SUCCESSFUL in 3s`
Otherwise inspect an error and fix your source file.

Then you run compiled file with next command: 
```
./gradlew :runCompiled
```
you should see generated xml right before `BUILD SUCCESSFUL in Xs` message

### EBNF
[See here](ebnf)

### Language
Language is quite similar to Lisp xml generation DSL:

You can see quick sample [here](language-example)

this is how you define element called *report*:
```
[report]
```
next xml will be generated:
```xml
<report>
</report>
```
then you can add attributes:
```
[report :name 'field-name' :id 'id-1'] 
```
generated as
```xml
<report name="field-name" id="id-1">
</report>
```
and add some content to elements:
```
[report :name 'field-name' :id 'id-1'
   [square '30.0']
]
```
generated as:
```xml
<report name="field-name" id="id-1">
  <square>30.0<square>
</report>
```

the key trait is lists which are always so verbose in xml.
You define list as 
```
{'first' 'second' 'third'}
```
you can have nested lists: 
```
{{'John' 'Smith'} {'Tom' 'Ford'}}
```
You use lists inside `for-each` construction and access elements through anonymous parameter `it`:
```
[report
  for-each {'first' 'second' 'third'}
    [value it]]
```
generated as:
```xml
<report>
        <value>
                first
        </value>
        <value>
                second
        </value>
        <value>
                third
        </value>
</report>
```
you can access child lists elements through index access on `it` parameter like `it.0`
so with such code:
```
[report
    [field :name 'field-name'
        [square '30.0']
        [groups
            for-each {'first' 'second' 'third'}
                [group it]]

        [coordinates
            for-each {{'30' '20'} {'31' '21'}}
                [coordinate
                    [lat it.0]
                    [lon it.1]]]]
]
```
you can generate next xml:
```xml
<report>
        <field name="field-name">
                <square>
                        30.0
                </square>
                <groups>
                        <group>
                                first
                        </group>
                        <group>
                                second
                        </group>
                        <group>
                                third
                        </group>
                </groups>
                <coordinates>
                        <coordinate>
                                <lat>
                                        30
                                </lat>
                                <lon>
                                        20
                                </lon>
                        </coordinate>
                        <coordinate>
                                <lat>
                                        31
                                </lat>
                                <lon>
                                        21
                                </lon>
                        </coordinate>
                </coordinates>
        </field>
</report>
```

### Project structure
[Lexical analysis](src/main/kotlin/lexer)

[Syntatic analysis](src/main/kotlin/parser)

[Semantic analysis](src/main/kotlin/semantic)

[Code generator](src/main/kotlin/generator)


Good luck :wink:
