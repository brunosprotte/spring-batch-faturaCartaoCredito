package com.faturaCartaoCreditoJob.step;

import com.faturaCartaoCreditoJob.dominio.FaturaCartaoCredito;
import com.faturaCartaoCreditoJob.dominio.Transacao;
import com.faturaCartaoCreditoJob.reader.FaturaCartaoCreditoReader;
import com.faturaCartaoCreditoJob.writer.TotalTransacoesFooterCallback;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FaturaCartaoCreditoStepConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step faturaCartaoCreditoStep(
            final ItemStreamReader<Transacao> lerTransacoesReader,
            final ItemProcessor<FaturaCartaoCredito, FaturaCartaoCredito> carregarDadosClienteProcessor,
            final ItemWriter<FaturaCartaoCredito> escreverFaturaCredito,
            final TotalTransacoesFooterCallback listener
    ) {
        return stepBuilderFactory
                .get("faturaCartaoCreditoStep")
                .<FaturaCartaoCredito, FaturaCartaoCredito>chunk(1)
                .reader(new FaturaCartaoCreditoReader(lerTransacoesReader))
                .processor(carregarDadosClienteProcessor)
                .writer(escreverFaturaCredito)
                .listener(listener)
                .build();
    }


}
