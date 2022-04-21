package uk.ac.man.library.oacpv2.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "audit")


@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Audit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	private Date created_date;

	private String created_by;

	private String type;

	private String message;

	private String level;

	@Column(length = 20480)
	private String updateDetail;
	
	public String getUpdateDetail() {
		return updateDetail;
	}

	public void setUpdateDetail(String updateDetail) {
		this.updateDetail = updateDetail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
