package com.company.medicine.security;

import com.company.medicine.entity.Medicine;
import com.company.medicine.entity.Sale;
import com.company.medicine.entity.SaleItem;
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

    @MenuPolicy(menuIds = {"PosView", "Stock.list", "Medicine.list", "LowStockView", "OutOfStock", "ExpiredMedicine", "ExpiringSoon", "DashboardView", "SaleItem.list", "Sale.list", "datatl_entityInspectorListView"})
    @ViewPolicy(viewIds = {"Medicine.list", "Stock.list", "PosView", "LowStockView", "OutOfStock", "ExpiredMedicine", "ExpiringSoon", "LoginView", "DashboardView", "MainView", "Stock.detail", "SaleItem.list", "Sale.list", "datatl_entityInspectorListView", "Sale.detail", "SaleItem.detail", "Medicine.detail"})
    void screens();

    @EntityAttributePolicy(entityClass = Stock.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Stock.class, actions = EntityPolicyAction.ALL)
    void stock();

    @SpecificPolicy(resources = "ui.loginToUi")
    void specific();

    @EntityAttributePolicy(entityClass = Medicine.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Medicine.class, actions = EntityPolicyAction.ALL)
    void medicine();

    @EntityAttributePolicy(entityClass = Sale.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Sale.class, actions = EntityPolicyAction.ALL)
    void sale();

    @EntityAttributePolicy(entityClass = SaleItem.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = SaleItem.class, actions = EntityPolicyAction.ALL)
    void saleItem();
}