
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

    _root.saveBudRole = function( identity, pack, role, data, callback ) {
        var url = jsRoutes.controllers.BudRoles.saveBudRole( identity, pack, role ).url;
        console.log( "QiBud.saveBudRole() on " + url );
        $.ajax( {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            url: url,
            data: JSON.stringify( data ),
            success: function( response ) {
                callback( response );
            }
        } );
    };

    _root.invokeBudRoleAction = function( identity, pack, role, action, data, callback ) {
        var url = jsRoutes.controllers.BudRoles.invokeBudRoleAction( identity, pack, role, action ).url;
        console.log( "QiBud.invokeBudRoleAction() on " + url );
        $.ajax( {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            url: url,
            data: JSON.stringify( data ),
            success: function( response ) {
                callback( response );
            }
        } );
    };

    _root.packs = {};

})(roles)

// Do whatever should be done on each page
$(document).ready( function() {

    console.log( "UI Layer Started!" );

    console.log( "Bud Roles SPI:" );
    console.log( roles );

} );
