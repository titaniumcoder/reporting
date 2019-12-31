import React from "react";
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from "react-router-dom";

export default function App() {
    return (
        <Router>
            <div>
                <nav>
                    <li>
                        <Link to="/">Home</Link>
                    </li>
                    <li>
                        <Link to="/about">About</Link>
                    </li>
                    <li>
                        <Link to="/users">Users</Link>
                    </li>
                </nav>

                <Switch>
                    <Route path="/about">
                        <div>About</div>
                    </Route>
                    <Route path="/users">
                        <div>Users</div>
                    </Route>
                    <Route path="/">
                        <div>Home</div>
                    </Route>
                </Switch>
            </div>
        </Router>
    )
}
