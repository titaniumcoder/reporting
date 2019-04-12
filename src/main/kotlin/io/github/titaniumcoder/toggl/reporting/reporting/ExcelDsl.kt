package io.github.titaniumcoder.toggl.reporting.reporting

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream

// TODO implement the whole magical dsl here!
class Excel {
    private val sheets = arrayListOf<Sheet>()

    fun sheet(name: String, init: Sheet.() -> Unit): Sheet {
        val sheet = Sheet(name)
        sheet.init()
        sheets.add(sheet)
        return sheet
    }

    fun render(): ByteArray {
        val workbook = XSSFWorkbook()
        for (s in sheets) {
            s.render(workbook)
        }

        ByteArrayOutputStream().use {
            workbook.write(it)
            return it.toByteArray()
        }
    }
}

class Sheet(val name: String) {
    fun render(workbook: XSSFWorkbook) {
        val sheet = workbook.createSheet(name)

        // TODO handle the sheet content
    }
}

fun excel(init: Excel.() -> Unit): Excel {
    val excel = Excel()
    excel.init()
    return excel
}