
@file:JvmName("GenerateXml")
import org.redundent.kotlin.xml.xml

fun main() {
    val xml = xml("report") {
"field"{
	attribute("name", "field-name")
		
	"square"{
		-"30.0"
		}
		"groups"{
		val list0 = listOf("first", "second", "third")
			for (element0 in list0)
			"group"{
				-element0.toString()
				}
		}
		"coordinates"{
		val list1 = listOf(listOf("30", "20"), listOf("31", "21"))
			for (element1 in list1)
			"coordinate"{
				"lat"{
					-element1[0].toString()
					}
					"lon"{
					-element1[1].toString()
					}
				}
		}
	}
}


    println(xml)
}
