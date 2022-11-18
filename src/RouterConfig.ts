// PAGES
import HomePage from "./pages/HomePage.svelte";
import StandardPage from "./pages/StandardPage.svelte";
import AdminPage from "./pages/AdminPage.svelte";
import NotFoundPage from "./pages/NotFoundPage.svelte";

// ICONS
import Home from "carbon-icons-svelte/lib/Home.svelte";
import DocumentBlank from "carbon-icons-svelte/lib/DocumentBlank.svelte";
import WarningAlt from "carbon-icons-svelte/lib/WarningAlt.svelte";

const routerConfig = [
    {
        type: "route",
        route: {
            "/" : HomePage
        },
        icon: Home,
        title: "Home Page",
        description: "Pagina principale di SchoolBoard.",
        requiredRoles: ["sb_user"],
        children: [],
    },
    {
        type: "route",
        route: {
            "/standard" : StandardPage
        },
        icon: DocumentBlank,
        title: "Standard Page",
        description: "Pagina standard di SchoolBoard.",
        requiredRoles: ["sb_user"],
        children: [
            {
                route: {
                    "/standard/sub" : StandardPage
                },
                icon: DocumentBlank,
                title: "Standard Sub Page",
                description: "Sottopagina standard di SchoolBoard.",
                requiredRoles: ["sb_user"],
            },
            {
                route: {
                    "/standard/subadm" : AdminPage
                },
                icon: DocumentBlank,
                title: "Standard Sub Admin Page",
                description: "Wow queste descrizioni sono complicate!",
                requiredRoles: ["sb_user", "sb_admin"],
            },
        ],
    },
    {
        type: "divider"
    },
    {
        type: "route",
        route: {
            "/admin" : AdminPage
        },
        icon: WarningAlt,
        title: "Admin Page",
        description: "Pagina admin di SchoolBoard.",
        requiredRoles: ["sb_user", "sb_admin"],
        children: [],
    },
    {
        type: "route-nm",
        requiredRoles: ["sb_user", "sb_admin"],
        route: {
            "/admin/:id" : AdminPage,
        }
    },
    {
        type: "route-nm",
        requiredRoles: ["sb_user"],
        route: {
            "/standard/sub" : StandardPage,
        }
    },
    {
        type: "route-nm",
        requiredRoles: ["sb_user", "sb_admin"],
        route: {
            "/standard/subadm" : AdminPage,
        }
    },
    {
        type: "route-nm",
        requiredRoles: ["sb_user"],
        route: {
            "*" : NotFoundPage,
        }
    }
]

// ###########################################################

let checker = (arr, target) => target.every(v => arr.includes(v));

let getAllRoutes = (isNm : boolean) => {
    let fatherRoutes = null;
    if(isNm){
        fatherRoutes = routerConfig.filter(route => route.type == "route" || route.type == "route-nm");
    }else{
        fatherRoutes = routerConfig.filter(route => route.type == "route");
    }
    let childRoutes = fatherRoutes.map((children) => {
        if(children.children != undefined && children.children.length > 0){
            return children.children;
        }
    })
    childRoutes = childRoutes.filter(route => route != undefined);
    childRoutes.forEach((route) => {
        route.forEach((subRoute) => {
            fatherRoutes.push(subRoute)
        })
    })
    return fatherRoutes;
}

export function getMenu(roles: Array<string>){
    let tf = routerConfig.filter(route => route.type == "route" || route.type == "divider");
    tf = tf.filter(route => {
        if(route.type == "divider"){
            return route;
        }
        if(checker(roles, route.requiredRoles)){
            // useless information stripping
            delete route.requiredRoles;
            delete route.description;
            let href = {
                href: Object.getOwnPropertyNames(route.route).at(0)
            }
            Object.assign(route, href);
            delete route.route;
            return route;
        }
    });
    tf = tf.map(route => {
        if(route.type == "divider"){
            return route;
        }
        if(route.children.length > 0){
            route.children = route.children.filter(children => {
                if (checker(roles, children.requiredRoles)) {
                    // useless information stripping
                    delete children.requiredRoles;
                    delete children.description;
                    let href = {
                        href: Object.getOwnPropertyNames(children.route).at(0)
                    }
                    Object.assign(children, href);
                    delete children.route;
                    return children;
                }
            });
            return route;
        }else{
            return route;
        }
    });
    return tf;
}

export function getRouter(roles: Array<string>){
    let routes = {};
    getAllRoutes(true).map((route) => {
        if(checker(roles, route.requiredRoles)) Object.assign(routes, route.route)
    });
    return routes;
}

export function getPageTitleLookupTable(){
    let lt = [];
    getAllRoutes(false).map((route) => {
        lt.push({
            href : Object.getOwnPropertyNames(route.route).at(0),
            title: route.title
        });
    });
    return lt;
}

export function getSearchBarContent(roles: Array<string>){
    let routes = [];
    getAllRoutes(false).map((route) => {
        if(checker(roles, route.requiredRoles)) {
            let obj = {
                //@ts-ignore
                href: Object.getOwnPropertyNames(route.route).at(0),
                text: route.title,
                description: route.description,
            };
            routes.push(obj);
        }
    });
    return routes;
}

export default {getRouter, getSearchBarContent, getPageTitleLookupTable, getMenu}