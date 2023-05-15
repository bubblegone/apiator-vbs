package com.apiator.vbs.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>{

    @Query("select  c from Card c WHERE c.number = ?1")
    Optional<Card> findCardByNumber(String number);

    @Query("select c from Card c where c.owner.id = ?1")
    List<Card> findAllByOwner(UUID id);

    @Modifying
    @Query("delete from Card c WHERE c.number = ?1")
    void deleteCardByNumber(String number);

    @Modifying
    @Transactional
    @Query("update Card c set c.pinHash = ?2 where c.number = ?1")
    void updateCardPinByNumber(String number, String pinHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Card c set c.balance = ?2 where c.number = ?1")
    void updateCardBalanceByNumber(String number, int newBalance);
}
