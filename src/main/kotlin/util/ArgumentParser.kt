package util

import kotlin.reflect.KClass
import kotlin.system.exitProcess

class ArgumentParser(
    private val originalArguments: Array<String>,
    private val description: String? = null,
) {
    private val arguments = mutableListOf<Argument>()

    init {
        addSingleArgument(
            name = "-h",
            longName = "--help",
            help = "show this help message"
        )
    }

    fun addValueArgument (
        name: String,
        longName: String = name,
        valueName: String,
        help: String,
        defaultValue: String? = null,
        isRequire: Boolean = false
    ) {
        arguments.add(
            Argument(
                name = name,
                longName = longName,
                valueName = valueName,
                help = help,
                hasValue = true,
                defaultValue = defaultValue,
                isRequire = isRequire,
            )
        )
    }

    fun addSingleArgument (
        name: String,
        longName: String = name,
        help: String,
        isRequired: Boolean = false
    ) {
        arguments.add(
            Argument(
                name = name,
                longName = longName,
                valueName = "",
                help = help,
                hasValue = false,
                defaultValue = null,
                isRequire = isRequired,
            )
        )
    }

    fun parse(): Map<String, String> {
        val remainArguments = originalArguments.toMutableList()
        val parsedMap = mutableMapOf<String, String>()

        for(argument in arguments) {
            if(argument.isRequire && remainArguments.firstOrNull() == null) {
                println("Argument [${argument.name}] must be specified.")
                exitProcess(-1)
            }

            if(remainArguments.firstOrNull() == null) continue

            if(isArgNameMatch(remainArguments.first(), argument.name, argument.longName)) {
                if(argument.longName == "--help") {
                    help()
                    exitProcess(0)
                }

                if(argument.hasValue) {
                    val value = remainArguments.elementAtOrNull(1)

                    if(value != null) {
                        parsedMap[argument.name] = value
                        remainArguments.removeFirst()
                        remainArguments.removeFirst()
                    } else {
                        println("Argument [${argument.name}] must always specify a value [${argument.valueName}]")
                        exitProcess(-1)
                    }
                } else {
                    parsedMap[argument.name] = remainArguments.first()
                    remainArguments.removeFirst()
                }
            } else if(argument.isRequire) {
                parsedMap[argument.name] = remainArguments.first()
                remainArguments.removeFirst()
            }
        }

        return parsedMap
    }

    fun help() {
        print("Usage: sample.jar ")

        for (argument in arguments) {
            if(argument.hasValue) {
                print("[${argument.name} ${argument.valueName}] ")
            } else {
                print("[${argument.name}] ")
            }
        }

        println()
        println(description)
        println()

        println("Require arguments:")

        for (argument in arguments.filter { it.isRequire }) {
            if(argument.hasValue) {
                println("\t${argument.name} ${argument.valueName}, ${argument.longName} ${argument.valueName}\t${argument.help}")
            } else {
                println("\t${argument.name} ${argument.valueName}\t\t${argument.help}")
            }
        }

        println()
        println("options: ")

        for (argument in arguments.filter { !it.isRequire }) {
            if(argument.hasValue) {
                println("\t${argument.name} ${argument.valueName}, ${argument.longName} ${argument.valueName}\t${argument.help}")
            } else {
                println("\t${argument.name} ${argument.valueName}\t\t${argument.help}")
            }
        }
    }

    private fun isArgNameMatch(originalName: String, shortName: String, longName: String): Boolean {
        return (originalName == shortName) || (originalName.lowercase() == longName.lowercase())
    }

    private data class Argument(
        val name: String,
        val longName: String,
        val valueName: String,
        val help: String,
        val hasValue: Boolean,
        val defaultValue: String?,
        val isRequire: Boolean
    )
}