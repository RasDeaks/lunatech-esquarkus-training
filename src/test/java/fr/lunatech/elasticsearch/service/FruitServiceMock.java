package fr.lunatech.elasticsearch.service;

import fr.lunatech.elasticsearch.model.Fruit;
import io.quarkus.test.Mock;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Mock
@ApplicationScoped
public class FruitServiceMock extends FruitService{

    @Override
    public List<Fruit> searchByName(String name) throws IOException {
        Fruit mockResult = new Fruit();
        mockResult.setName("Pasteque de test");
        mockResult.setDescription("Pas vraiment une pastèque");
        return Collections.singletonList(mockResult);
    }
}
