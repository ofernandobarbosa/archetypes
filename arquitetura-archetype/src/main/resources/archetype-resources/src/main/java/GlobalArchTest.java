package ${package};

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Teste de arquitetura reutilizável.
 * Scaneia tudo sob '${groupId}' no classpath do módulo que o importa.
 */
@AnalyzeClasses(packages = "${groupId}")
public class GlobalArchTest {

    @ArchTest
    static final ArchRule isolamento_dominio = RegrasDddBase.ISOLAMENTO_DO_DOMINIO;

    @ArchTest
    static final ArchRule fluxo_application = RegrasDddBase.FLUXO_APPLICATION;

    @ArchTest
    static final ArchRule nomenclatura_repository = RegrasDddBase.NOMENCLATURA_REPOSITORY;

    @ArchTest
    static final ArchRule nomenclatura_usecase = RegrasDddBase.NOMENCLATURA_USECASE;

    @ArchTest
    static final ArchRule entidades_puras = RegrasDddBase.ENTIDADES_PURAS;

    @ArchTest
    static final ArchRule isolamento_infra = RegrasDddBase.ISOLAMENTO_INFRA;

    @ArchTest
    static final ArchRule recursos_rest_api = RegrasDddBase.RECURSOS_REST_NA_API;
}
