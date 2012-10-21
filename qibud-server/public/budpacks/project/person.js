(function(){
    roles.packs.project.person = {

        inject_view: function( bud_identity, target, descriptor, state ) {

            var $view = $("#"+target);

            $view.append( "<h3>"+descriptor.description+"</h3>" );
            // $view.append(  "<h4>Role Descriptor</h4><pre>" + JSON.stringify( descriptor ) + "</pre>" );

            // Role State
            $view.append(  "<h4>State</h4>" );
            // $view.append(  "<pre>" + JSON.stringify( state ) + "</pre>" );
            // name state
            $view.append( '<p><input type="text" value="' + state.name + '" id="'+target+'-state-name"/> <a href="#" class="btn btn-mini" id="'+target+'-state-save">Save</a></p>' );
            $view.find( '#' + target + '-state-save' ).click( function( event ) {
                event.preventDefault();
                var name = $view.find( '#' + target + '-state-name' ).val();
                var json = {
                    name: name
                };
                roles.saveBudRole( bud_identity, descriptor['budpack-name'], descriptor['role-name'], json, function( response ) {
                    document.location.reload( true );
                } );
            });

            // Role Actions
            $view.append( '<h4>Actions</h4>' );
            // say action
            var action = descriptor.actions["say"];
            $view.append( '<p><input type="text" id="'+target+'-action-'+action.name+'-input" value="Hello World!"/> <a href="#" class="btn btn-mini" id="'+target+'-action-'+action.name+'">'+action.name+'</a></p>' );
            $view.find( "#"+target+"-action-"+action.name ).click( function( event ) {
                event.preventDefault();
                var message = $view.find( "#"+target+"-action-"+action.name+"-input" ).val();
                var json = {
                    message: message
                };
                roles.invokeBudRoleAction( bud_identity, descriptor['budpack-name'], descriptor['role-name'], action.name, json, function( response ) {
                    alert( response.message );
                } );
            });

        }

    };
})();
