package org.nds.openehr.composer.factory;

import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectVersionId;
import com.nedap.archie.rm.support.identification.PartyRef;
import com.nedap.archie.rm.support.identification.TerminologyId;
import com.nedap.archie.xml.JAXBUtil;
import org.ehrbase.client.building.OptSkeletonBuilder;
import org.nds.openehr.composer.jackson.JSONUtil;
import org.openehr.schemas.v1.OPERATIONALTEMPLATE;
import org.openehr.schemas.v1.TemplateDocument;

import javax.xml.bind.Marshaller;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;


public class CompositionFactory {

    public static Composition generateComposition(String template) throws Exception {

        return createComposition(TemplateDocument.Factory.parse(template).getTemplate());
    }

    public static Composition generateComposition(InputStream template) throws Exception {

        return createComposition(TemplateDocument.Factory.parse(template).getTemplate());
    }

    private static Composition createComposition(OPERATIONALTEMPLATE template) {
        Composition composition;

        OptSkeletonBuilder cut = new OptSkeletonBuilder();

        composition = (Composition) cut.generate(template);

        composition.setUid(new ObjectVersionId(UUID.randomUUID().toString()));
        composition.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1", null), "en"));
        composition.setTerritory(new CodePhrase(new TerminologyId("ISO_3166-1", null), "GB"));
        composition.getCategory().setValue("event");
        composition.setComposer(new PartyIdentified(new PartyRef(new HierObjectId("1234-5678"),"DEMOGRAPHIC", "PERSON"),"Composer",null));
        composition.getContext().setStartTime(new DvDateTime(currentTime()));
        composition.getContext().setSetting(new DvCodedText("other care", new CodePhrase(new TerminologyId("openehr", null), "238")));
        composition.getContext().setHealthCareFacility(null);
        composition.getContext().setLocation(null);
        composition.getContext().setEndTime(null);
        composition.getContext().setOtherContext(null);
        composition.getContext().setParticipations(null);

        return composition;
    }

    private static String currentTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        return dateTimeFormatter.format(new Date().toInstant());
    }

    public static String formatJSON(Composition composition) throws Exception {
        return JSONUtil.getObjectMapper().writeValueAsString(composition);
    }

    public static String formatXML(Composition composition) throws Exception {
        Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(composition, writer);

        return writer.toString();
    }
}
