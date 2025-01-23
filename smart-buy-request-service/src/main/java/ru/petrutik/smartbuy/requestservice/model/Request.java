package ru.petrutik.smartbuy.requestservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer requestNumber;
    private String searchQuery;
    private BigDecimal maxPrice;
    private boolean isUpdated;
    @OneToMany
    @JoinTable(name = "requests_results",
            joinColumns = @JoinColumn(
                    name = "request_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "product_id",
                    referencedColumnName = "id"
            )
    )
    private List<Product> result;
    @OneToMany
    @JoinTable(name = "requests_bans",
            joinColumns = @JoinColumn(
                    name = "request_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "product_id",
                    referencedColumnName = "id"
            )
    )
    private List<Product> banned;
}
