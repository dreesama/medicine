package com.company.medicine.security;

import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "Full Access", code = FullAccessRole.CODE)
public interface FullAccessRole {

    String CODE = "system-full-access";

    @EntityPolicy(entityName = "*", actions = {EntityPolicyAction.ALL})
    @EntityAttributePolicy(entityName = "*", attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @ViewPolicy(viewIds = {"DashboardView", "Medicine.list", "Stock.list", "PosView", "LowStockView", "OutOfStock", "ExpiringSoon", "ExpiredMedicine", "User.list", "flowui_DateIntervalDialog", "entityInfoView", "datatl_entityInspectorDetailView", "flowui_PropertyFilterCondition.detail", "flowui_JpqlFilterCondition.detail", "flowui_AddConditionView", "flowui_GroupFilterCondition.detail", "headerPropertyFilterLayout", "inputDialog", "multiValueSelectDialog", "sec_ResourceRoleModel.detail", "sec_ResourceRoleModel.lookup", "sec_RowLevelRoleModel.detail", "sec_RowLevelRoleModel.lookup", "resetPasswordView", "changePasswordView", "sec_EntityAttributeResourcePolicyModel.detail", "sec_EntityResourcePolicyModel.detail", "sec_ViewResourcePolicyModel.detail", "sec_GraphQLResourcePolicyModel.detail", "sec_MenuResourcePolicyModel.detail", "sec_ViewResourcePolicyModel.create", "sec_MenuResourcePolicyModel.create", "sec_ResourcePolicyModel.detail", "sec_EntityAttributeResourcePolicyModel.create", "sec_SpecificResourcePolicyModel.detail", "sec_EntityResourcePolicyModel.create", "roleAssignmentView", "sec_RowLevelPolicyModel.detail", "sec_UserSubstitution.detail", "sec_UserSubstitution.view", "MainView", "Stock.detail", "Medicine.detail", "LoginView", "User.detail", "report_InputParametersDialogView", "report_ReportGroup.detail", "report_WizardReportRegion.detail", "report_Report.detail", "report_ReportExecution.list", "report_ReportExecutionDialogView", "report_ReportTemplate.detail", "report_ReportInputParameter.detail", "report_ReportImportDialogView", "report_ReportWizardCreatorView", "report_ScriptEditorView", "report_EntityTreeNode.list", "report_QueryParameter.detail", "report_ReportValueFormat.detail"})
    @MenuPolicy(menuIds = {"DashboardView", "Medicine.list", "Stock.list", "PosView", "LowStockView", "OutOfStock", "ExpiringSoon", "ExpiredMedicine", "User.list"})
    @SpecificPolicy(resources = "*")
    void fullAccess();
}