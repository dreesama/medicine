package com.company.medicine.security;

import com.company.medicine.entity.Medicine;
import com.company.medicine.entity.Stock;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "UnAuthorizedUser", code = UnAuthorizedUserRole.CODE)
public interface UnAuthorizedUserRole {
    String CODE = "un-authorized-user";

    @MenuPolicy(menuIds = {"PosView", "Stock.list", "Medicine.list", "LowStockView", "OutOfStock", "ExpiredMedicine", "ExpiringSoon", "DashboardView"})
    @ViewPolicy(viewIds = {"Medicine.list", "Stock.list", "PosView", "LowStockView", "OutOfStock", "ExpiredMedicine", "ExpiringSoon", "LoginView", "Medicine.detail", "DashboardView", "MainView"})
    void screens();

    @EntityAttributePolicy(entityClass = Medicine.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Medicine.class, actions = EntityPolicyAction.ALL)
    void medicine();

    @EntityAttributePolicy(entityClass = Stock.class, attributes = {"id", "activeIngredientName", "activeIngredientStrength", "dosageForm", "price", "quantity", "expirationDate", "brandName", "medicine", "*"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Stock.class, actions = EntityPolicyAction.ALL)
    void stock();

    @SpecificPolicy(resources = "ui.loginToUi")
    void specific();
}