package com.swp.company.repository;


import com.swp.company.dto.response.UserCountByMonthDto;
import com.swp.company.entity.User;
import com.swp.company.util.common.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.department d " +
            "LEFT JOIN u.role r " +
            "WHERE LOWER(u.name) LIKE LOWER(concat('%', :name ,'%')) " +
            "AND (:department = 0 OR d.id = :department) " +
            "AND (:role = 0 OR r.id = :role) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:note = -1 OR (:note = 1 AND u.note IS NOT NULL) OR (:note = 0 AND u.note IS NULL)) " +
            "AND (r.id != 5)")
    Page<User> findAllWithFilter(@Param("name") String name,
                                 @Param("department") int department,
                                 @Param("role") int role,
                                 @Param("status") UserStatus status,
                                 @Param("note") int note,
                                 Pageable pageable);
    @Query("SELECT u FROM User u JOIN u.role r LEFT JOIN u.documents d WHERE r.name = 'Ứng viên' and u.isDeleted = 0")
    Page<User> findAllCandidate(Pageable pageable);

    @Query("delete from Document d where d.user.id = :id and d.type = :type ")
    void deleteDocument(@Param("id") int id, @Param("type") String type);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id <> 5")
    int countEmployee();

    @Query(value = """
            SELECT
                      CONCAT(LPAD(MONTH(u.created_at), 2, '0'), '/', YEAR(u.created_at)) AS `date`,
                      COUNT(*) AS count
                  FROM
                      user u
                  WHERE
                      u.role_id = 5
                  GROUP BY
                      `date`
                  ORDER BY
                      STR_TO_DATE(`date`, '%m/%Y');
    """, nativeQuery = true)
    List<Object[]> countUsersByMonthWithRole5Subquery();

    @Query(value = """
        SELECT r.name AS roleName, COUNT(u.id) AS userCount
        FROM user u
        JOIN role r ON u.role_id = r.id
        GROUP BY r.name
        ORDER BY r.name
    """, nativeQuery = true)
    List<Object[]> countUsersByRole();
}
