document.addEventListener('DOMContentLoaded', () => {
    const supplyModuleConfig = {
        filterFormId: 'filter-form-supply',
        tableContentId: 'supply-list-content',
        defaultPaginateUrl: '/supplies/table',
        ajaxSuccessCallback: function() {
            console.log("Tabla de suministros recargada y callbacks generales ejecutados.");
        }
    };

    if (typeof attachGenericTableListeners === 'function') {
        attachGenericTableListeners(supplyModuleConfig);
    } else {
        console.error("La función attachGenericTableListeners no está definida. Asegúrate que dafault.js se carga antes que Supply.js.");
    }
});