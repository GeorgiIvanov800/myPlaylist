// function updateHiddenInput(actionType, value, userId) {
//     console.log("Function called with actionType:", actionType);
//     console.log("Function called with value:", value);
//     console.log("Function called with userId:", userId);
//
//     console.log("Function called: ", actionType, value, userId); // This will log the values to the console
//     if(actionType === 'add') {
//         console.log("Setting hiddenAddRoleId value to:", value);
//         document.getElementById('hiddenAddRoleId' + userId).value = value;
//     } else if(actionType === 'remove') {
//         console.log("Setting hiddenRemoveRoleId value to:", value);
//         document.getElementById('hiddenRemoveRoleId'+ userId).value = value;
//     }
// }

function updateHiddenRoleId(roleId, userId) {
    console.log("Setting hiddenRoleId value to:", roleId);
    let element = document.getElementById('hiddenRoleId' + userId);
    if (element) {
        element.value = roleId;
    } else {
        console.error("Element with ID 'hiddenRoleId" + userId + "' not found");
    }
}
function handleActionChange(action, userId) {
    document.getElementById('addRoleSelect' + userId).style.display = action === 'addRole' ? 'inherit' : 'none';
    document.getElementById('removeRoleSelect' + userId).style.display = action === 'removeRole' ? 'inherit' : 'none';
}