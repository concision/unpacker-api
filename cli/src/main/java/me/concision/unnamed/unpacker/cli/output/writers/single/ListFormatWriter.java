package me.concision.unnamed.unpacker.cli.output.writers.single;

import lombok.RequiredArgsConstructor;
import me.concision.unnamed.unpacker.api.Lua2JsonConverter;
import me.concision.unnamed.unpacker.api.PackageParser.PackageEntry;
import me.concision.unnamed.unpacker.cli.Unpacker;
import me.concision.unnamed.unpacker.cli.output.FormatType;
import org.bson.Document;
import org.json.JSONArray;

/**
 * See {@link FormatType#LIST}
 *
 * @author Concision
 */
@RequiredArgsConstructor
public class ListFormatWriter extends SingleRecordFormatWriter {
    private final Unpacker unpacker;
    private final JSONArray list = new JSONArray();

    @Override
    public void publish(Unpacker unpacker, PackageEntry record) {
        Document document = new Document();

        document.put("path", record.absolutePath());
        if (unpacker.args().skipJsonificiation) {
            document.put("package", record.contents());
        } else {
            document.put("package", Lua2JsonConverter.parse(record.contents(), unpacker.args().convertStringLiterals));
        }

        list.put(document);
    }

    @Override
    public void close() {
        if (unpacker.args().prettifyJson) {
            outputStream.print(list.toString(2));
        } else {
            outputStream.print(list.toString());
        }
        super.close();
    }
}
