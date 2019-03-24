
String method = "chunked" // "plain"  "chunked"
String rate = "500K"
Integer parallelCommands = 8

String command = "./parallel_commands.sh "

parallelCommands.times {
    command += "\"curl -v --limit-rate $rate localhost:8080/zip/${method} -o output-${it}.zip\" "
}

println command