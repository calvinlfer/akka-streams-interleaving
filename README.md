# Akka Streams: Interleaving streams with priority
This is a set of examples to demonstrate to make use of Akka Streams built-in stages to achieve interleaving
stream behavior to give priority to one lane of data amidst many lanes of data. 

There are 2 examples:
-  `JsonFileStreamingExample.scala` is a simple single stream of data which reads JSON data from a static file, parses
the data into a Scala `case class` and renders it to screen. This is present to get you familiar with the project.

![image](https://user-images.githubusercontent.com/14280155/28602637-35f8310e-718d-11e7-93c0-4b23511dea7e.png)

- `MergePreferredStreamInterleavingExample.scala` is the main focus of this project. It demonstrates how to combine 
multiple `Source`s together and give priority to a single `Source`. This could be especially useful if you are trying
to combine sources of data coming from batch jobs and real-time data and you would like to give priority to the 
real-time stream.

![image](https://user-images.githubusercontent.com/14280155/28602648-49095ebc-718d-11e7-9f3a-9a66fd504930.png)

## Contributions and PRs
Please feel free to contribute by sending a Pull Request or filing an issue.
