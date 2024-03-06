package com.sample.repository;

import com.sample.dto.response.UserDetailResponse;
import com.sample.model.UserEntity;
import com.sample.repository.criteria.SearchCriteria;
import com.sample.repository.criteria.UserSearchQueryCriteriaConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Search user by criteria
     *
     * @param params list of filter conditions
     * @return list of users
     */
    public Page<UserEntity> findAllUsersByCriteria(Pageable pageable, List<SearchCriteria> params) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
        final Root<UserEntity> r = query.from(UserEntity.class);

        Predicate predicate = builder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.where(predicate);

        // This query fetches the Users as per the Page Limit
         List<UserEntity> userEntityList = entityManager.createQuery(query)
                 .setFirstResult((int) pageable.getOffset())
                 .setMaxResults(pageable.getPageSize())
                 .getResultList();

        // Create count query
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<UserEntity> usersRootCount = countQuery.from(UserEntity.class);
        countQuery.select(builder.count(usersRootCount)).where(predicate);

        // Fetches the count of all UserEntity as per given criteria
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(userEntityList, pageable, count);
    }

    /**
     * Find users by customize JPA query
     *
     * @param firstName
     * @param lastName
     * @param gender
     * @param pageNo
     * @param pageSize
     * @return list of users
     */
    public Page<UserDetailResponse> findAllUsersByCustomizeQuery(String firstName, String lastName, Integer gender, int pageNo, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");

        if (StringUtils.hasLength(firstName)) {
            where.append(" AND u.firstName=:firstName");
        }
        if (StringUtils.hasLength(lastName)) {
            where.append(" AND u.lastName=:lastName");
        }
        if (null != gender) {
            where.append(" AND u.gender=:gender");
        }

        // Get list of users
        Query selectQuery = entityManager.createQuery(String.format("SELECT new com.sample.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone) FROM UserEntity u %s ORDER BY u.id DESC", where));
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if (where.toString().contains("AND")) {
            if (StringUtils.hasLength(firstName)) {
                selectQuery.setParameter(":firstName", firstName);
            }
            if (StringUtils.hasLength(lastName)) {
                selectQuery.setParameter(":lastName", lastName);
            }
            if (null != gender) {
                selectQuery.setParameter(":gender", gender);
            }
        }
        List<UserDetailResponse> userList = selectQuery.getResultList();

        // Count users
        Long count = (Long) entityManager.createQuery(String.format("SELECT COUNT(*) FROM UserEntity u %s", where)).getSingleResult();

        return new PageImpl<>(userList, PageRequest.of(pageNo,pageSize), count);
    }
}
