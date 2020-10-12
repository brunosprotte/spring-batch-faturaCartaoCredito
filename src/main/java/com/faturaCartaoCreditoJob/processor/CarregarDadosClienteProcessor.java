package com.faturaCartaoCreditoJob.processor;

import com.faturaCartaoCreditoJob.dominio.Cliente;
import com.faturaCartaoCreditoJob.dominio.FaturaCartaoCredito;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CarregarDadosClienteProcessor implements ItemProcessor<FaturaCartaoCredito, FaturaCartaoCredito> {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public FaturaCartaoCredito process(FaturaCartaoCredito faturaCartaoCredito) throws Exception {
        String uri = String.format("http://my-json-server.typicode.com/giuliana-bezerra/demo/profile/%d", faturaCartaoCredito.getCliente().getId());
        ResponseEntity<Cliente> response = restTemplate.getForEntity(uri, Cliente.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new ValidationException(String.format("Cliente não encontrado: %d", faturaCartaoCredito.getCliente().getId()));

        faturaCartaoCredito.setCliente(response.getBody());
        return faturaCartaoCredito;
    }
}
