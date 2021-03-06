package com.eden.orchid.api.options;

import com.caseyjbrooks.clog.Clog;
import com.eden.common.json.JSONElement;
import com.eden.common.util.EdenPair;
import com.eden.common.util.EdenUtils;
import com.eden.krow.KrowTable;
import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.options.annotations.Archetype;
import com.eden.orchid.api.options.annotations.Description;
import com.eden.orchid.api.options.annotations.Option;
import com.eden.orchid.api.options.annotations.OptionsData;
import com.eden.orchid.api.options.annotations.Validate;
import com.eden.orchid.api.registration.Prioritized;
import com.eden.orchid.utilities.OrchidUtils;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class OptionsExtractor {

    private final OrchidContext context;
    private final List<OptionExtractor> extractors;
    private final List<OptionValidator> validators;

    @Inject
    public OptionsExtractor(OrchidContext context, Set<OptionExtractor> extractors, Set<OptionValidator> validators) {
        this.context = context;
        this.extractors = new ArrayList<>(extractors);
        this.extractors.sort(Comparator.comparing(Prioritized::getPriority).reversed());

        this.validators = new ArrayList<>(validators);
        this.validators.sort(Comparator.comparing(Prioritized::getPriority).reversed());
    }

    public void extractOptions(OptionsHolder optionsHolder, JSONObject options) {
        // setup initial options
        JSONObject initialOptions = new JSONObject(options.toMap());
        JSONObject archetypalOptions = loadArchetypalData(optionsHolder, initialOptions);

        JSONObject actualOptions = OrchidUtils.merge(archetypalOptions, initialOptions);

        // extract options fields
        EdenPair<Field, Set<Field>> fields = findOptionFields(optionsHolder.getClass());

        if(fields.first != null) {
            setOptionValue(optionsHolder, fields.first, fields.first.getName(), JSONElement.class, new JSONElement(actualOptions));
        }

        for (Field field : fields.second) {
            String key = (!EdenUtils.isEmpty(field.getAnnotation(Option.class).value()))
                    ? field.getAnnotation(Option.class).value()
                    : field.getName();

            if (field.getType().isArray()) {
                setOptionArray(optionsHolder, field, actualOptions, key);
            }
            else if (List.class.isAssignableFrom(field.getType())) {
                setOptionArray(optionsHolder, field, actualOptions, key);
            }
            else {
                setOption(optionsHolder, field, actualOptions, key);
            }
        }
    }

    public List<OptionsDescription> describeOptions(Class<?> optionsHolderClass) {
        EdenPair<Field, Set<Field>> fields = findOptionFields(optionsHolderClass);

        List<OptionsDescription> optionDescriptions = new ArrayList<>();

        if(fields.first != null) {
            optionDescriptions.add(new OptionsDescription(fields.first.getName(), JSONElement.class, "All options passed to this object.", "{}"));
        }

        for (Field field : fields.second) {
            String key = (!EdenUtils.isEmpty(field.getAnnotation(Option.class).value()))
                    ? field.getAnnotation(Option.class).value()
                    : field.getName();
            String description = (field.getAnnotation(Description.class) != null && !EdenUtils.isEmpty(field.getAnnotation(Description.class).value()))
                    ? field.getAnnotation(Description.class).value()
                    : "";
            String defaultValue = "N/A";

            for (OptionExtractor extractor : extractors) {
                if (extractor.acceptsClass(field.getType())) {
                    defaultValue = extractor.describeDefaultValue(field);
                    break;
                }
            }

            optionDescriptions.add(new OptionsDescription(key, field.getType(), description, defaultValue));
        }

        optionDescriptions.sort(Comparator.comparing(OptionsDescription::getKey));

        return optionDescriptions;
    }

    public KrowTable getDescriptionTable(Class<?> optionsHolderClass) {
        KrowTable table = new KrowTable();

        List<OptionsDescription> options = describeOptions(optionsHolderClass);

        options.forEach(option -> {
            table.cell("Type", option.getKey(), cell -> {cell.setContent(option.getOptionType().getSimpleName()); return null;});
            table.cell("Default Value", option.getKey(), cell -> {cell.setContent(option.getDefaultValue()); return null;});
            table.cell("Description", option.getKey(), cell -> {cell.setContent(option.getDescription()); return null;});
        });

        table.column("Description", cell -> {cell.setWrapTextAt(45); return null;});
        table.column("Type", cell -> {cell.setWrapTextAt(15); return null;});
        table.column("Default Value", cell -> {cell.setWrapTextAt(15); return null;});

        return table;
    }

    public List<String> getOptionNames(Class<?> optionsHolderClass) {
        EdenPair<Field, Set<Field>> fields = findOptionFields(optionsHolderClass);

        List<String> optionNames = new ArrayList<>();

        for (Field field : fields.second) {
            String key = (!EdenUtils.isEmpty(field.getAnnotation(Option.class).value()))
                    ? field.getAnnotation(Option.class).value()
                    : field.getName();

            optionNames.add(key);
        }

        return optionNames;
    }

    private EdenPair<Field, Set<Field>> findOptionFields(Class<?> optionsHolderClass) {
        Field optionsDataField = null;
        Set<Field> fields = new HashSet<>();

        while (optionsHolderClass != null) {
            Field[] declaredFields = optionsHolderClass.getDeclaredFields();
            if (!EdenUtils.isEmpty(declaredFields)) {
                for (Field field : declaredFields) {
                    if (field.isAnnotationPresent(Option.class)) {
                        fields.add(field);
                    }
                    else if (field.isAnnotationPresent(OptionsData.class) && field.getType().equals(JSONElement.class)) {
                        optionsDataField = field;
                    }
                }
            }

            optionsHolderClass = optionsHolderClass.getSuperclass();
        }

        return new EdenPair<>(optionsDataField, fields);
    }

    private JSONObject loadArchetypalData(Object target, JSONObject actualOptions) {
        Class<?> optionsHolderClass = target.getClass();
        JSONObject allAdditionalData = new JSONObject();

        List<Archetype> archetypeAnnotations = new ArrayList<>();

        while (optionsHolderClass != null) {
            Collections.addAll(archetypeAnnotations, optionsHolderClass.getAnnotationsByType(Archetype.class));
            optionsHolderClass = optionsHolderClass.getSuperclass();
        }

        Collections.reverse(archetypeAnnotations);

        for(Archetype archetype : archetypeAnnotations) {
            OptionArchetype archetypeDataProvider = context.getInjector().getInstance(archetype.value());

            JSONObject archetypeConfiguration;
            if(actualOptions.has(archetype.key()) && actualOptions.get(archetype.key()) instanceof JSONObject) {
                archetypeConfiguration = actualOptions.getJSONObject(archetype.key());
            }
            else {
                archetypeConfiguration = new JSONObject();
            }

            archetypeDataProvider.extractOptions(context, archetypeConfiguration);
            JSONObject archetypalData = archetypeDataProvider.getOptions(target, archetype.key());

            if(archetypalData != null) {
                allAdditionalData = OrchidUtils.merge(allAdditionalData, archetypalData);
            }
        }

        return allAdditionalData;
    }

    private void setOptionArray(OptionsHolder optionsHolder, Field field, JSONObject options, String key) {
        boolean foundExtractor = false;
        for (OptionExtractor extractor : extractors) {
            if (extractor.acceptsClass(field.getType())) {
                setOptionValue(optionsHolder, field, key, field.getType(), extractor.getArray(field, options, key));
                foundExtractor = true;
                break;
            }
        }

        if (!foundExtractor) {
            setOptionValue(optionsHolder, field, key, field.getType(), null);
        }
    }

    private void setOption(OptionsHolder optionsHolder, Field field, JSONObject options, String key) {
        boolean foundExtractor = false;
        for (OptionExtractor extractor : extractors) {
            if (extractor.acceptsClass(field.getType())) {
                Object object = extractor.getOption(field, options, key);
                setOptionValue(optionsHolder, field, key, field.getType(), object);
                foundExtractor = true;
                break;
            }
        }

        if (!foundExtractor) {
            setOptionValue(optionsHolder, field, key, field.getType(), null);
        }
    }

    private boolean validateOptionValue(Field field, String key, Object value) {
        EdenPair<Boolean, List<ValidationResult>> validationResults = null;
        boolean isValid = true;
        boolean throwIfInvalid = false;

        if (field.isAnnotationPresent(Validate.class)) {
            Validate validateAnnotation = field.getAnnotation(Validate.class);

            throwIfInvalid = validateAnnotation.throwIfInvalid();

            List<EdenPair<OptionValidator, String[]>> fieldValidators = new ArrayList<>();

            if (!EdenUtils.isEmpty(validateAnnotation.value())) {
                for (String rule : validateAnnotation.value()) {
                    if (!EdenUtils.isEmpty(rule)) {
                        EdenPair<OptionValidator, String[]> validator = getValidator(rule);
                        if (validator != null && validator.first != null) {
                            fieldValidators.add(validator);
                        }
                    }
                }
            }
            if (!EdenUtils.isEmpty(validateAnnotation.rules())) {
                for (Class<? extends OptionValidator> rule : validateAnnotation.rules()) {
                    if (rule != null) {
                        EdenPair<OptionValidator, String[]> validator = getValidator(rule);
                        if (validator != null && validator.first != null) {
                            fieldValidators.add(validator);
                        }
                    }
                }
            }

            List<ValidationResult> results = new ArrayList<>();

            for (EdenPair<OptionValidator, String[]> validator : fieldValidators) {
                if (validator.first.acceptsClass(field.getType())) {
                    ValidationResult result = validator.first.validate(key, value, validator.second);
                    if (result != null) {
                        results.add(result);
                        isValid = isValid && result.isValid();
                    }
                    else {
                        isValid = false;
                    }
                }
                else {
                    Clog.e("Validator class {} used on a field that it cannot validate (field {} in class {}).", validator.first.getClass().getSimpleName(), field.getName(), field.getDeclaringClass().getName());
                }
            }

            validationResults = new EdenPair<>(isValid, results);
        }

        if (validationResults == null) {
            validationResults = new EdenPair<>(true, null);
        }

        if (!validationResults.first) {
            String message = Clog.format("{} in class {} failed validation for the following reasons:\n", key, field.getDeclaringClass().getName());
            for (ValidationResult result : validationResults.second) {
                message += " * " + result.getMessage() + "\n";
            }

            if (throwIfInvalid) {
                throw new RuntimeException(message);
            }
            else {
                Clog.e(message);
            }
        }

        return validationResults.first;
    }

    private void setOptionValue(OptionsHolder optionsHolder, Field field, String key, Class<?> objectClass, Object value) {
        if(!validateOptionValue(field, key, value)) {
            value = null;
        }

        try {
            String setterMethodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Method method = optionsHolder.getClass().getMethod(setterMethodName, objectClass);
            method.invoke(optionsHolder, value);
            return;
        }
        catch (NoSuchMethodException e) {
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            field.set(optionsHolder, value);
        }
        catch (IllegalAccessException e) {
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private EdenPair<OptionValidator, String[]> getValidator(String rule) {
        String[] ruleParts = rule.split(":");
        if (ruleParts.length > 0) {
            for (OptionValidator validator : validators) {
                if (validator.getKey().equals(ruleParts[0])) {
                    return new EdenPair<>(validator, Arrays.copyOfRange(ruleParts, 1, ruleParts.length));
                }
            }
        }

        Clog.w("could not find validator for {}", rule);
        return null;
    }

    private EdenPair<OptionValidator, String[]> getValidator(Class<? extends OptionValidator> rule) {
        for (OptionValidator validator : validators) {
            if (validator.getClass().equals(rule)) {
                return new EdenPair<>(validator, new String[0]);
            }
        }

        Clog.w("could not find validator for class {}", rule.getName());
        return null;
    }

}
