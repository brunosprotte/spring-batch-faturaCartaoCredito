package com.faturaCartaoCreditoJob.writer;

import com.faturaCartaoCreditoJob.dominio.FaturaCartaoCredito;
import com.faturaCartaoCreditoJob.dominio.Transacao;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

@Configuration
public class ArquivoFaturaCartaoCreditoWriterConfig {

    @Bean
    public MultiResourceItemWriter<FaturaCartaoCredito> arquivosFaturaCartaoCredito() {
        return new MultiResourceItemWriterBuilder<FaturaCartaoCredito>()
                .name("arquivosFaturaCartaoCredito")
                .resource(new FileSystemResource("files/fatura"))
                .itemCountLimitPerResource(1)
                .resourceSuffixCreator(suffixCreator())
                .delegate(arquivofaturaCartaoCredito())
                .build();
    }

    private ResourceAwareItemWriterItemStream<? super FaturaCartaoCredito> arquivofaturaCartaoCredito() {
        return new FlatFileItemWriterBuilder<FaturaCartaoCredito>()
                .name("arquivofaturaCartaoCredito")
                .resource(new FileSystemResource("files/fatura.txt"))
                .lineAggregator(lineAggregator())
                .headerCallback(headerCallback())
                .footerCallback(footerCallback())
                .build();
    }

    @Bean
    public FlatFileFooterCallback footerCallback() {
        return new TotalTransacoesFooterCallback();
    }

    private FlatFileHeaderCallback headerCallback() {
        return new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer
                        .append(String.format("%121s\n", "Cartão XPTO"))
                        .append(String.format("%121s\n\n", "Rua Vergueiro, 131"));
            }
        };
    }

    private LineAggregator<FaturaCartaoCredito> lineAggregator() {

        return new LineAggregator<FaturaCartaoCredito>() {
            @Override
            public String aggregate(FaturaCartaoCredito faturaCartaoCredito) {
                StringBuilder writer = new StringBuilder();

                writer.append(String.format("Nome: %s\n", faturaCartaoCredito.getCliente().getNome()))
                        .append(String.format("Endereço: %s\n\n\n", faturaCartaoCredito.getCliente().getEndereco()))
                        .append(String.format("Fatura completa do cartão: %d\n", faturaCartaoCredito.getCartaoCredito().getNumeroCartaoCredito()))
                        .append("----------------------------------------------------------------------------------------------------------------------\n")
                        .append("DATA DESCRICAO VALOR\n")
                        .append("----------------------------------------------------------------------------------------------------------------------\n");

                for (Transacao t : faturaCartaoCredito.getTransacoes()) {
                    writer.append(String.format("\n[%10s] %-80ss - %s",
                            new SimpleDateFormat("dd/MM/yyyy").format(t.getData()),
                            t.getDescricao(),
                            NumberFormat.getCurrencyInstance().format(t.getValor())));
                }
                return writer.toString();

            }
        };

    }

    private ResourceSuffixCreator suffixCreator() {
        return new ResourceSuffixCreator() {
            @Override
            public String getSuffix(int i) {
                return i + ".txt";
            }
        };
    }

}
