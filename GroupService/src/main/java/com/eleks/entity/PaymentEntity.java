package com.eleks.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "payment_description", length = 200)
    private String paymentDescription;

    @NotNull
    @Column(name = "price")
    private Double price;

    @Builder.Default
    @Column(name = "co_payers")
    private List<Long> coPayers = new ArrayList<>();

    @NotNull
    @Column(name = "creator_id")
    private Long creatorId;

    @NotNull
    @Column(name = "timestamp")
    private LocalDateTime timeStamp;

    @Column(name = "group_id")
    private Long groupId;
}
