package me.concision.warframe.decacher.output;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.concision.warframe.decacher.output.writers.multi.FlattenedFormatWriter;
import me.concision.warframe.decacher.output.writers.multi.RecursiveFormatWriter;
import me.concision.warframe.decacher.output.writers.single.BinaryFormatWriter;
import me.concision.warframe.decacher.output.writers.single.ListFormatWriter;
import me.concision.warframe.decacher.output.writers.single.MapFormatWriter;
import me.concision.warframe.decacher.output.writers.single.PathsFormatWriter;
import me.concision.warframe.decacher.output.writers.single.RecordsFormatWriter;

/**
 * Specifies the output format type for a specified {@link OutputMode}
 *
 * @author Concision
 * @date 10/9/2019
 */
@RequiredArgsConstructor
public enum FormatType {
    // single
    /**
     * Outputs Package.bin raw decompressed file
     */
    BINARY(OutputMode.SINGLE, BinaryFormatWriter::new),
    /**
     * Outputs matching package paths on each line
     * (e.g. /Lotus/Path/.../PackageName\r\n)
     */
    PATHS(OutputMode.SINGLE, PathsFormatWriter::new),
    /**
     * Outputs a matching JSON record on each line
     * (e.g. {"path": "/Lotus/Path/...", "package": ...}\r\n)
     */
    RECORDS(OutputMode.SINGLE, RecordsFormatWriter::new),
    /**
     * Outputs all matching packages into a JSON map
     * (e.g. {"/Lotus/Path/...": ..., ...})
     */
    MAP(OutputMode.SINGLE, MapFormatWriter::new),
    /**
     * Outputs all matching packages into a JSON array
     * (e.g. [{"path": "/Lotus/Path/...", "package": ...}, ...])
     */
    LIST(OutputMode.SINGLE, ListFormatWriter::new),

    // multiple
    /**
     * Outputs each matching package as a file with replicated directory structure
     * (e.g. ${--output-location}/Lotus/Path/.../PackageName)
     */
    RECURSIVE(OutputMode.MULTIPLE, RecursiveFormatWriter::new),
    /**
     * Outputs each matching package as a file without replicating directory structure
     * (e.g. ${--output-location}/PackageName)
     */
    FLATTENED(OutputMode.MULTIPLE, FlattenedFormatWriter::new);

    /**
     * Associated output mode
     */
    @Getter
    @NonNull
    private final OutputMode mode;

    /**
     * Type-specific format writer instance generator
     */
    @NonNull
    private final Supplier<OutputFormatWriter> writerSupplier;

    /**
     * Constructs a new output format writer
     *
     * @return a new {@link OutputFormatWriter} instance
     */
    public OutputFormatWriter newWriter() {
        return writerSupplier.get();
    }


    /**
     * Specifies how output is directed to destinations during the decaching process
     */
    public enum OutputMode {
        /**
         * Outputs to a single destination (file/STDOUT)
         */
        SINGLE,
        /**
         * Outputs to a single destination (directory)
         */
        MULTIPLE
    }
}