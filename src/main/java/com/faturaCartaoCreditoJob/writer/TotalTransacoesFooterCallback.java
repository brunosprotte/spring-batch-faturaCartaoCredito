package com.faturaCartaoCreditoJob.writer;

import com.faturaCartaoCreditoJob.dominio.FaturaCartaoCredito;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.file.FlatFileFooterCallback;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.List;

public class TotalTransacoesFooterCallback implements FlatFileFooterCallback {

    private Double total = 0.0;

    @Override
    public void writeFooter(final Writer writer) throws IOException {
        writer.write(String.format("\n%121s", "Total: " + NumberFormat.getCurrencyInstance().format(total)));
    }

    @BeforeWrite
    public void beforeWrite(final List<FaturaCartaoCredito> faturas) {

        for (FaturaCartaoCredito faturaCartaoCredito : faturas) {
            total += faturaCartaoCredito.getTotal();
        }
    }

    @AfterChunk
    public void afterChunk(final ChunkContext chunkContext) {
        total = 0.0;
    }

}
