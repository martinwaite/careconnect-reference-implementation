package uk.nhs.careconnect.ri.entity.carePlan;

import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;
import uk.nhs.careconnect.ri.entity.carePlan.CarePlanEntity;

import javax.persistence.*;

@Entity
@Table(name="CarePlanCondition", uniqueConstraints= @UniqueConstraint(name="PK_CAREPLAN_REASON", columnNames={"CAREPLAN_REASON_ID"})
        ,indexes = { @Index(name="IDX_CAREPLAN_CONDITION", columnList = "CONDITIONS_CONDITION_ID")}
)
public class CarePlanCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "CAREPLAN_REASON_ID")
    private Long Id;

    @ManyToOne
    @JoinColumn (name = "CAREPLAN_ID",foreignKey= @ForeignKey(name="FK_CAREPLAN_CONDITIONS_CAREPLAN_ID"))
    private CarePlanEntity carePlan;


    @ManyToOne
    @JoinColumn (name = "CONDITIONS_CONDITION_ID", nullable = false, foreignKey= @ForeignKey(name="FK_CAREPLAN_CONDITIONS_CONDITION_ID"))
    private ConditionEntity condition;


    public void setId(Long id) {
        Id = id;
    }

    public Long getId() {
        return Id;
    }

    public CarePlanCondition setCarePlan(CarePlanEntity carePlan) {
        this.carePlan = carePlan;
        return this;
    }

    public CarePlanCondition setCondition(ConditionEntity condition) {
        this.condition = condition;
        return this;
    }

    public ConditionEntity getCondition() {
        return condition;
    }

    public CarePlanEntity getCarePlan() {
        return carePlan;
    }
}
