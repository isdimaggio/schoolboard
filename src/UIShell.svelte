<script lang="ts">
  import "carbon-components-svelte/css/all.css";

  import Router, {push} from "svelte-spa-router";
  import { userInfo, rolesTable } from './store'
  import Keycloak from "keycloak-js";
  import {
      getRouter,
      getSearchBarContent,
      getPageTitleLookupTable,
      getMenu
  } from './RouterConfig';
  import {roleConfig} from './RoleConfig';

  // TODO: should be loaded from ENV
  let kc = new Keycloak({
      url: 'https://sso.flowopia.com',
      realm: 'dev',
      clientId: 'core'
  });

  let logged_in                 = null;
  let routes                    = {};
  let searchBarTable            = [];
  let menuContent               = [];
  let pageLookupTable           = getPageTitleLookupTable();
  let currentPage               = window.location.href.split("#")[1].split("&")[0]; // ugly hack because svelte-spa-router...
                                                                                    // ...has problems with $location :/
  let currentPageTitle          = "Benvenuto!";

  let getPageTitle = (href) => {
      if (logged_in) {
          try {
              return pageLookupTable.filter(page => page.href == href).at(0).title;
          }catch (lol) {
              return "Benvenuto!"; // if something goes terribly wrong
          }
      }else{
          return "Benvenuto!";
      }
  };

  let updateCurrentPage = (href) => {
      currentPage = href;
      push(href);
      currentPageTitle = getPageTitle(href);
  };

  kc.init({ onLoad: "check-sso" }).then((auth) => {
      logged_in = auth;
      if (auth) {
          logged_in = true;
          kc.loadUserInfo().then((user) => {
              //@ts-ignore
              user.token = kc.idToken;
              userInfo.set(user);
              // check all existing roles
              let roles = roleConfig.filter((role) => kc.hasRealmRole(role));
              rolesTable.set(roles);
              // create routing table
              routes = getRouter(roles);
              // create search table
              searchBarTable = getSearchBarContent(roles);
              // update page title
              currentPageTitle = getPageTitle(currentPage);
              // update menu entries
              menuContent = getMenu(roles);
          });
      }
  });

  import {
      Theme,
      Header,
      HeaderUtilities,
      HeaderSearch,
      SideNav,
      SideNavItems,
      SideNavLink,
      SideNavDivider,
      SideNavMenu,
      SideNavMenuItem,
      SkipToContent,
      Content,
      HeaderAction,
      HeaderGlobalAction,
      HeaderPanelLinks,
      HeaderPanelDivider,
      HeaderPanelLink,
      Modal,
      Select,
      SelectItem,
      SelectItemGroup,
  } from "carbon-components-svelte";

  // ICONS
  import UserAvatarFilledAlt from "carbon-icons-svelte/lib/UserAvatarFilledAlt.svelte";
  import UserSettings from "carbon-icons-svelte/lib/UserSettings.svelte";
  import ColorSwitch from "carbon-icons-svelte/lib/ColorSwitch.svelte";
  import Logout from "carbon-icons-svelte/lib/Logout.svelte";
  import Login from "carbon-icons-svelte/lib/Login.svelte";
  import CalendarSettings from "carbon-icons-svelte/lib/CalendarSettings.svelte";

  let isSideNavOpen             = false;
  let isUserSettingsMenuOpen    = false;
  let shellTheme                = "white"; // default theme
  let themeSelectionModal       = false;
  let searchBarRef              = null;
  let isSearchBarActive         = false;
  let searchBarValue            = "";
  let selectedResultIndex       = 0;

  $: lowerCaseValue = searchBarValue.toLowerCase();
  $: results =
      searchBarValue.length > 0
          ? searchBarTable.filter((item) => {
              return (
                  item.text.toLowerCase().includes(lowerCaseValue) ||
                  item.description.includes(lowerCaseValue)
              );
          })
          : [];

</script>

<Theme bind:theme={shellTheme} persist persistKey="__carbon-theme" />

<Header company="SchoolBoard - " platformName={currentPageTitle} bind:isSideNavOpen>
    <svelte:fragment slot="skip-to-content">
        <SkipToContent />
    </svelte:fragment>
    <HeaderUtilities>
        <!-- searchbar (ONLY LOGGED IN) -->
        {#if logged_in && $userInfo.preferred_username}
        <HeaderSearch
                bind:ref={searchBarRef}
                bind:active={isSearchBarActive}
                bind:value={searchBarValue}
                bind:selectedResultIndex
                placeholder="Ricerca pagine"
                {results}
                on:select={(e) => {
                    updateCurrentPage(e.detail.selectedResult.href);
                }}
        />
        {/if}
        <HeaderGlobalAction aria-label="Settings" icon={CalendarSettings} />
        <!-- user settings -->
        <HeaderAction
                bind:isOpen={isUserSettingsMenuOpen}
                icon={UserAvatarFilledAlt}
                closeIcon={UserAvatarFilledAlt}
        >
            <HeaderPanelLinks>
                {#if logged_in && $userInfo.preferred_username}
                    <HeaderPanelDivider>{$userInfo.preferred_username}</HeaderPanelDivider>
                    <HeaderPanelLink on:click={() => (kc.accountManagement())}>
                        <UserSettings class="userMenuItem" />
                        Impostazioni Account
                    </HeaderPanelLink>
                    <HeaderPanelLink on:click={() => (themeSelectionModal = true)} >
                        <ColorSwitch class="userMenuItem"/>
                        Impostazioni Tema
                    </HeaderPanelLink>
                    <HeaderPanelLink on:click={() => (kc.logout())}>
                        <Logout class="userMenuItem"/>
                        Esci
                    </HeaderPanelLink>
                {/if}

                {#if logged_in === false}
                    <HeaderPanelDivider>Accedi a SchoolBoard</HeaderPanelDivider>
                    <HeaderPanelLink on:click={() => (themeSelectionModal = true)} >
                        <ColorSwitch class="userMenuItem"/>
                        Impostazioni Tema
                    </HeaderPanelLink>
                    <HeaderPanelLink on:click={() => (kc.login())}>
                        <Login class="userMenuItem"/>
                        Accedi
                    </HeaderPanelLink>
                {/if}

            </HeaderPanelLinks>
        </HeaderAction>
    </HeaderUtilities>
</Header>

<!-- render only if logged in  -->
{#if logged_in && $userInfo.preferred_username}
<SideNav bind:isOpen={isSideNavOpen}>
    <SideNavItems>
        {#each menuContent as item}
            {#if item.type === "divider"}
                <SideNavDivider />
            {/if}
            {#if item.type === "route"}
                {#if item.children.length === 0}
                    <SideNavLink
                            icon={item.icon}
                            text={item.title}
                            on:click={() => (updateCurrentPage(item.href))}
                            isSelected="{currentPage === item.href}"
                    />
                {/if}
                {#if item.children.length > 0}
                    <SideNavMenu
                            icon={item.icon}
                            text={item.title}
                    >
                        <SideNavMenuItem
                                text={item.title}
                                on:click={() => (updateCurrentPage(item.href))}
                                isSelected="{currentPage === item.href}"
                        />
                        {#each item.children as children}
                            <SideNavMenuItem
                                    icon={children.icon}
                                    text={children.title}
                                    on:click={() => (updateCurrentPage(children.href))}
                                    isSelected="{currentPage === children.href}"
                            />
                        {/each}
                    </SideNavMenu>
                {/if}
            {/if}
        {/each}
    </SideNavItems>
</SideNav>
{/if}

<Modal passiveModal bind:open={themeSelectionModal} modalHeading="Seleziona il tema dell'interfaccia" on:open on:close>
    <Select labelText="Scegli un tema" bind:selected={shellTheme}>
        <SelectItemGroup label="Temi chiari">
            <SelectItem value="white" text="Bianco" />
            <SelectItem value="g10" text="Grigio chiaro" />
        </SelectItemGroup>
        <SelectItemGroup label="Temi scuri">
            <SelectItem value="g80" text="Grigio scuro" />
            <SelectItem value="g90" text="Grigio molto scuro" />
            <SelectItem value="g100" text="Nero" />
        </SelectItemGroup>
    </Select>
</Modal>

<Content>
    {#if logged_in && $userInfo.preferred_username}
        <Router {routes} />
    {/if}
    {#if logged_in === false}
        <h1>Benvenuto in SchoolBoard!</h1>
        <h2>Effettua l'accesso per continuare</h2>
    {/if}
</Content>