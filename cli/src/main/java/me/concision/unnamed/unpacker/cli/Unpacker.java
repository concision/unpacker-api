package me.concision.unnamed.unpacker.cli;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import me.concision.unnamed.unpacker.cli.output.OutputFormatWriter;

import java.io.InputStream;

/**
 * Control flow for unpacking process
 *
 * @author Concision
 */
@Log
@RequiredArgsConstructor
public class Unpacker {
    /**
     * Processed and validated command-line arguments
     */
    @NonNull
    @Getter
    private final CommandArguments args;

    /**
     * Execute decaching with namespaced parameters
     */
    @SneakyThrows
    public void execute() {
        // prepare environment
        this.prepare();
        // cache with environment
        this.decache();
    }

    /**
     * Prepares decaching environment
     */
    private void prepare() {
        System.setProperty("line.separator", "\r\n");
    }

    /**
     * Obtains Packages.bin input stream from source and writes it to destination
     */
    private void decache() {
        log.info("Generating Packages.bin stream");
        // generate packages.bin input stream
        InputStream packagesStream;
        try {
            packagesStream = args.sourceType.generate(args);

            if (packagesStream == null) {
                throw new NullPointerException("source type " + args.sourceType + " generated a null stream");
            }
        } catch (Throwable throwable) {
            throw new RuntimeException("failed to generate Packages.bin input stream", throwable);
        }

        // generate new writer
        log.info("Constructing format writer");
        OutputFormatWriter formatWriter = args.outputFormat.newWriter(this);

        log.info("Executing format writer");
        try (InputStream __ = packagesStream) {
            formatWriter.format(this, packagesStream);
        } catch (Throwable throwable) {
            throw new RuntimeException("formatter " + args.outputFormat + " failed to write", throwable);
        }
        log.info("Finished unpacking. Goodbye.");
    }
}
