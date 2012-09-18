
// Define global roles object with helper functions
var roles = {};
(function(_root){

    _root.budRole = function( identity, pack, role, callback ) {
        var url = jsRoutes.controllers.BudRoles.budRole( identity, pack, role ).url;
        console.log( "QiBud.budRole() on " + url );
        $.getJSON( url, function( response ) {
            callback( response );
        } );
    };

    // TODO Implement JS saveBudRole
    _root.saveBudRole = function( identity, pack, role, data ) {
        var url = jsRoutes.controllers.BudRoles.saveBudRole( identity, pack, role ).url;
        console.log( "QiBud.saveBudRole() on " + url );
    };

    // TODO Implement JS invokeBudRoleAction
    _root.invokeBudRoleAction = function( identity ,pack, role, action, data ) {
        var url = jsRoutes.controllers.BudRoles.invokeBudRoleAction( identity, pack, role, action ).url;
        console.log( "QiBud.invokeBudRoleAction() on " + url );
    };

})(roles)

// Do whatever should be done on each page
$(document).ready( function() {

    console.log( "QiBud" );

    console.log( "Javascript Routing:" );
    console.log( jsRoutes );

    console.log( "Bud Roles SPI:" );
    console.log( roles );

} );
