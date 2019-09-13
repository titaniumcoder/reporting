import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import 'bootstrap/dist/css/bootstrap.min.css';
import * as serviceWorker from './serviceWorker';

import {library} from '@fortawesome/fontawesome-svg-core'
import {faCheck, faCog, faFileExcel, faSignOutAlt, faUndo} from "@fortawesome/free-solid-svg-icons";

library.add(faFileExcel);
library.add(faCheck);
library.add(faUndo);
library.add(faSignOutAlt);
library.add(faCog);

ReactDOM.render(<App/>, document.getElementById('root'));

serviceWorker.unregister();
