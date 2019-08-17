package crimson.application.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.ToString.Exclude;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@NotNull(message="Company Name Should Not be null")
	@NotBlank(message="Company Name should not be blank")
	@Column(nullable = false, unique = true)
	private String companyName;
	
	
	private String GST;
	
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH , CascadeType.MERGE})
	private UserCategory userCategory;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH ,CascadeType.MERGE})
	private Address address;
	
	@OneToOne(fetch=FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private User user;
}
