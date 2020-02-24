package org.nds.openehr.composer.controller;

import com.nedap.archie.rm.composition.Composition;
import org.nds.openehr.composer.factory.CompositionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompositionController {

    @GetMapping("/composition")
    public String composition(@RequestBody String template) throws Exception {

        Composition composition = CompositionFactory.generateComposition(template);

        return CompositionFactory.formatJSON(composition);
    }
}
