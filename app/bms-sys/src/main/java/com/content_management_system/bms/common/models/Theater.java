package com.content_management_system.bms.common.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "theaters")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SQLDelete(sql = "UPDATE theaters SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Theater extends MasterData {
    private String name;
    private String location;

    @Embedded
    private SeatConfiguration seatConfiguration;

    private int capacity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalRows", column = @Column(name = "total_premium_rows")),
            @AttributeOverride(name = "totalPrice", column = @Column(name = "total_premium_price"))
    })
    private SeatInfo premiumSeat;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalRows", column = @Column(name = "total_regular_rows")),
            @AttributeOverride(name = "totalPrice", column = @Column(name = "total_regular_price"))
    })
    private SeatInfo regularSeat;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalRows", column = @Column(name = "total_economy_rows")),
            @AttributeOverride(name = "totalPrice", column = @Column(name = "total_economy_price"))
    })
    private SeatInfo economySeat;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalRows", column = @Column(name = "total_basic_rows")),
            @AttributeOverride(name = "totalPrice", column = @Column(name = "total_basic_price"))
    })
    private SeatInfo basicSeat;
}
