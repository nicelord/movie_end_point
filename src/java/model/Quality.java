/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author asus
 */
@Entity
public class Quality {

    @Id
    @GeneratedValue
    private Long qualityId;
    String quality;
    int rank;
    @OneToMany(mappedBy = "quality")
    private List<StreamLink> streamLinks;

    public Long getQualityId() {
        return qualityId;
    }

    public void setQualityId(Long qualityId) {
        this.qualityId = qualityId;
    }

    public List<StreamLink> getStreamLinks() {
        return streamLinks;
    }

    public void setStreamLinks(List<StreamLink> streamLinks) {
        this.streamLinks = streamLinks;
    }


    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Quality(String quality) {
        this.quality = quality;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    
    
}
