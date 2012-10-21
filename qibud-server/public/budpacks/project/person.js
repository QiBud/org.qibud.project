(function(){
    roles.packs.project.person = {
        inject_view: function( bud_identity, target, descriptor, state ) {
            var $view = $("#"+target);
            var view = "<h3>"+descriptor.description+"</h3>";
            //view += "<h4>Role Descriptor</h4><pre>"+JSON.stringify(descriptor)+"</pre>";
            view += "<h4>Role State</h4><pre>"+JSON.stringify(state)+"</pre>";
            view += '<h4>Actions</h4>';
            $view.append( view );
            for( var actionName in descriptor.actions ) {
                var action = descriptor.actions[actionName];
                var actionView = '<p><input type="text" id="'+target+'-action-'+action.name+'-input" value="Hello World!"/> <a href="#" class="btn btn-mini" id="'+target+'-action-'+action.name+'">'+action.name+'</a></p>';
                $view.append( actionView );
                $view.find( "#"+target+"-action-"+action.name ).click( function( event ) {
                    event.preventDefault();
                    var message = $view.find( "#"+target+"-action-"+action.name+"-input" ).val();
                    console.log("RUNNING ACTION "+action.name+" with input " + message)
                    roles.invokeBudRoleAction( bud_identity, descriptor['budpack-name'], descriptor['role-name'], action.name, {
                        message: message
                    }, function( response ) {
                        alert( response.message );
                    } );
                });
            }
        }
    };
})();
