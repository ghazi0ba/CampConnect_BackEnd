package com.example.campconnect_backend.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String url;
    private String altText;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camping_site_id")
    @JsonIgnore
    private CampingSite campingSite;
 
    public Image(String url, String altText, CampingSite site) {
        this.url = url;
        this.altText = altText;
        this.campingSite = site;
    }
}
