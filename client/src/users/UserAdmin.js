import React from 'react';

import './UserAdmin.css.css';

const UserAdmin = () => {
    return (
        <div className="userAdmin">
            <table>
                <thead>
                    <tr>
                        <th>Email</th>
                        <th>Admin?</th>
                        <th>Can Book?</th>
                        <th>Can See Money?</th>
                        <th className="text-right"><button>+</button></th>
                    </tr>
                </thead>
                <tbody>
                <tr>
                    <td>rico.metzger@gmail.com</td>
                    <td><FontawesomIcon icon="check"/></td>
                    <td><FontawesomIcon icon="check"/></td>
                    <td><FontawesomIcon icon="check"/></td>
                    <td><div className="btn-group"><button>Update</button><button>Delete</button></div></td>
                </tr>
                </tbody>
            </table>
        </div>
    );
};

export default UserAdmin;
