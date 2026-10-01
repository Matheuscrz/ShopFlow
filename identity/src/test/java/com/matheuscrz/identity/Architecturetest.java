package com.matheuscrz.identity;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Garante a regra de dependência da arquitetura hexagonal (ADR-020):
 *
 * <pre>
 * adapter.in ──> application.port.in <── application.service ──> application.port.out <── adapter.out
 *                         todos ──> domain
 * </pre>
 */
@AnalyzeClasses(
        packages = "com.matheuscrz.identity",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_is_framework_free =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "jakarta.validation..",
                            "com.fasterxml.jackson..",
                            "..application..",
                            "..adapter..",
                            "..config..")
                    .because("o domínio é Java puro e não conhece frameworks nem camadas externas");

    @ArchTest
    static final ArchRule application_does_not_know_adapters =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..config..")
                    .because("a aplicação só conhece o domínio e as próprias ports");

    @ArchTest
    static final ArchRule inbound_adapters_do_not_use_outbound_adapters =
            noClasses().that().resideInAPackage("..adapter.in..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter.out..")
                    .because("o controller deve falar com a aplicação por meio das ports de entrada");

    @ArchTest
    static final ArchRule outbound_adapters_do_not_use_inbound_adapters =
            noClasses().that().resideInAPackage("..adapter.out..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter.in..");
}