package com.faturaCartaoCreditoJob.reader;

import com.faturaCartaoCreditoJob.dominio.CartaoCredito;
import com.faturaCartaoCreditoJob.dominio.Cliente;
import com.faturaCartaoCreditoJob.dominio.Transacao;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class LerTransacoesReaderConfig {

    @Bean
    public JdbcCursorItemReader<Transacao> lerTransacoesReader(
            @Qualifier("appDataSource") final DataSource dataSource
    ) {
        return new JdbcCursorItemReaderBuilder<Transacao>()
                .name("lerTransacoesReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM transacao JOIN cartao_credito USING (numero_cartao_credito) ORDER BY numero_cartao_credito")
                .rowMapper(rowMapperTransacao())
                .build();
    }


    private RowMapper<Transacao> rowMapperTransacao() {
        return new RowMapper<Transacao>() {
            @Override
            public Transacao mapRow(ResultSet rs, int i) throws SQLException {

                final CartaoCredito cartaoCredito = new CartaoCredito();
                final Cliente cliente = new Cliente();
                final Transacao transacao = new Transacao();

                cliente.setId(rs.getInt("cliente"));

                cartaoCredito.setNumeroCartaoCredito(rs.getInt("numero_cartao_credito"));
                cartaoCredito.setCliente(cliente);

                transacao.setId(rs.getInt("id"));
                transacao.setCartaoCredito(cartaoCredito);
                transacao.setData(rs.getDate("data"));
                transacao.setValor(rs.getDouble("valor"));
                transacao.setDescricao(rs.getString("descricao"));

                return transacao;

            }
        };
    }

}
