package br.upf.todolist.facade;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Operações genéricas de persistência (mesmo padrão usado em aula).
 *
 * Diferenças em relação à versão da aula:
 * - não há commit(): com JTA quem controla a transação é o contêiner EJB;
 * - findAll() é protegido, porque ignora o dono dos dados. Os controllers
 *   devem usar os métodos específicos de cada Facade, que filtram pelo usuário.
 */
public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;

    protected AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    protected void create(T entity) {
        getEntityManager().persist(entity);
    }

    protected T edit(T entity) {
        return getEntityManager().merge(entity);
    }

    protected void remove(T entity) {
        EntityManager em = getEntityManager();
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    protected T find(Object id) {
        return id == null ? null : getEntityManager().find(entityClass, id);
    }

    /**
     * Referência gerenciada a uma entidade já existente (por exemplo, o usuário
     * logado), para usar em relacionamentos sem precisar carregá-la do banco.
     */
    protected <E> E referencia(Class<E> classe, Object id) {
        return getEntityManager().getReference(classe, id);
    }

    protected List<T> findAll() {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
